package app.entities;
/*
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class eksEntity {

    public class Actor {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false, name = "Entity name id")
        @EqualsAndHashCode.Include
        private Integer id;

        private String name;

        //@ManyToMany(mappedBy = "actors",fetch = FetchType.EAGER)
        //  Set<Movie> movies = new HashSet<>();
   }

     // Helper methods to keep both sides in sync
    public void addExperience(Experience exp) {
        experiences.add(exp);
        exp.setLeader(this);
    }

    public void removeExperience(Experience exp) {
        experiences.remove(exp);
        exp.setLeader(null);
    }


    another eks:
    package app.entities;

import app.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Table(name = "experience")

public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "experience_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private LocalDateTime startTime;
    private LocalDateTime endTime;


    private double latitude;
    private double longitude;

    @Column(nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private Leader leader;
}
package app.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "leader")
@Builder
public class Leader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "leader_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    private String email;


    private String phone;
    private int yearsOfExperience;

    @OneToMany(mappedBy = "leader", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Experience> experiences = new ArrayList<>();


    // Helper methods to keep both sides in sync
    public void addExperience(Experience exp) {
        experiences.add(exp);
        exp.setLeader(this);
    }

    public void removeExperience(Experience exp) {
        experiences.remove(exp);
        exp.setLeader(null);
    }

}


}

 */
