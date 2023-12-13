package hexlet.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.*;


import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "tasks")
public class Task implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Integer index;

    @NotBlank
    @ToString.Include
    private String name;

    @Column(columnDefinition = "TEXT")
    @Lob
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Include
    private TaskStatus taskStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Include
    private User assignee;

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    private Set<Label> labels = new HashSet<>();

    @CreatedDate
    private Date createdAt;

}
