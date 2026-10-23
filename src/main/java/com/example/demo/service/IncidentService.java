package com.example.demo.service;

import com.example.demo.dto.CommentDto;
import com.example.demo.dto.HistoryDto;
import com.example.demo.dto.IncidentRequest;
import com.example.demo.dto.IncidentResponse;
import com.example.demo.entity.*;
import com.example.demo.enums.IncidentStatus;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.repository.IncidentCommentRepository;
import com.example.demo.repository.IncidentHistoryRepository;
import com.example.demo.repository.IncidentRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepo;
    private final IncidentHistoryRepository historyRepo;
    private final IncidentCommentRepository commentRepo;
    private final AppUserRepository userRepo;

    public IncidentService(IncidentRepository incidentRepo, IncidentHistoryRepository historyRepo,
                           IncidentCommentRepository commentRepo, AppUserRepository userRepo) {
        this.incidentRepo = incidentRepo;
        this.historyRepo = historyRepo;
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
    }

    private AppUser currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    private Incident find(Long id) {
        return incidentRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Incident introuvable"));
    }

    private boolean isAssignedTo(Incident i, AppUser u) {
        return i.getAssignedTo() != null && i.getAssignedTo().getId().equals(u.getId());
    }

    private void log(Incident i, String msg) {
        IncidentHistory h = new IncidentHistory();
        h.setIncident(i);
        h.setMessage(msg);
        historyRepo.save(h);
    }

    @Transactional
    public IncidentResponse create(IncidentRequest r) {
        AppUser me = currentUser();
        Incident i = new Incident();
        i.setTitle(r.title());
        i.setDescription(r.description());
        i.setCategory(r.category());
        i.setPriority(r.priority());
        i.setCreatedBy(me);
        incidentRepo.save(i);
        log(i, "Incident créé par " + me.getFullName());
        return toDto(i, false);
    }

    @Transactional()
    public List<IncidentResponse> my() {
        return incidentRepo.findByCreatedByOrderByCreatedAtDesc(currentUser())
                .stream().map(i -> toDto(i, false)).toList();
    }

    @Transactional()
    public List<IncidentResponse> assigned() {
        return incidentRepo.findByAssignedToOrderByCreatedAtDesc(currentUser())
                .stream().map(i -> toDto(i, false)).toList();
    }

    @Transactional()
    public List<IncidentResponse> all() {
        return incidentRepo.findAllByOrderByCreatedAtDesc()
                .stream().map(i -> toDto(i, false)).toList();
    }

    @Transactional()
    public IncidentResponse get(Long id) {
        Incident i = find(id);
        AppUser me = currentUser();
        boolean allowed = switch (me.getRole()) {
            case ADMIN -> true;
            case EMPLOYEE -> i.getCreatedBy().getId().equals(me.getId());
            case TECHNICIAN -> isAssignedTo(i, me);
        };
        if (!allowed) throw new ApiException(HttpStatus.FORBIDDEN, "Accès interdit");
        return toDto(i, true);
    }

    @Transactional
    public IncidentResponse updateStatus(Long id, IncidentStatus newStatus) {
        Incident i = find(id);
        AppUser me = currentUser();
        if (me.getRole() == Role.TECHNICIAN && !isAssignedTo(i, me)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Cet incident ne vous est pas affecté");
        }
        if (i.getStatus() == IncidentStatus.CLOSED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Incident clôturé : statut non modifiable");
        }
        String old = i.getStatus().name();
        i.setStatus(newStatus);
        i.setUpdatedAt(LocalDateTime.now());
        log(i, old + " -> " + newStatus);
        return toDto(i, true);
    }

    @Transactional
    public IncidentResponse assign(Long id, Long technicianId) {
        Incident i = find(id);
        AppUser tech = userRepo.findById(technicianId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        if (tech.getRole() != Role.TECHNICIAN) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cet utilisateur n'est pas un technicien");
        }
        if (i.getStatus() == IncidentStatus.CLOSED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Incident clôturé");
        }
        i.setAssignedTo(tech);
        if (i.getStatus() == IncidentStatus.OPEN) i.setStatus(IncidentStatus.ASSIGNED);
        i.setUpdatedAt(LocalDateTime.now());
        log(i, "Affecté à " + tech.getFullName());
        return toDto(i, true);
    }

    @Transactional
    public IncidentResponse addComment(Long id, String content) {
        Incident i = find(id);
        AppUser me = currentUser();
        if (!isAssignedTo(i, me)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Cet incident ne vous est pas affecté");
        }
        IncidentComment c = new IncidentComment();
        c.setIncident(i);
        c.setAuthor(me);
        c.setContent(content);
        commentRepo.save(c);
        return toDto(i, true);
    }

    private IncidentResponse toDto(Incident i, boolean detail) {
        List<HistoryDto> history = detail
                ? historyRepo.findByIncidentOrderByCreatedAtAsc(i).stream()
                .map(h -> new HistoryDto(h.getMessage(), h.getCreatedAt())).toList()
                : List.of();
        List<CommentDto> comments = detail
                ? commentRepo.findByIncidentOrderByCreatedAtAsc(i).stream()
                .map(c -> new CommentDto(c.getAuthor().getFullName(), c.getContent(), c.getCreatedAt())).toList()
                : List.of();
        return new IncidentResponse(i.getId(), i.getTitle(), i.getDescription(),
                i.getCategory(), i.getPriority(), i.getStatus(),
                i.getCreatedAt(), i.getUpdatedAt(),
                i.getCreatedBy().getFullName(),
                i.getAssignedTo() == null ? null : i.getAssignedTo().getFullName(),
                history, comments);
    }
}
