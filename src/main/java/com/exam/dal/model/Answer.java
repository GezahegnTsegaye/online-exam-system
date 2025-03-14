package com.exam.dal.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text_answer", length = 1000)
    private String textAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToMany
    @JoinTable(
            name = "answer_options",
            joinColumns = @JoinColumn(name = "answer_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private Set<Option> selectedOptions = new HashSet<>();

    /**
     * Adds a selected option to the answer
     * @param option The option to add
     */
    public void addSelectedOption(Option option) {
        selectedOptions.add(option);
    }

    /**
     * Removes a selected option from the answer
     * @param option The option to remove
     */
    public void removeSelectedOption(Option option) {
        selectedOptions.remove(option);
    }

    /**
     * Sets multiple selected options
     * @param options Set of options to be selected
     */
    public void setSelectedOptions(Set<Option> options) {
        this.selectedOptions.clear();
        if (options != null) {
            this.selectedOptions.addAll(options);
        }
    }

    /**
     * Get selected option IDs
     * @return Set of selected option IDs
     */
    public Set<Long> getSelectedOptionIds() {
        return selectedOptions.stream()
                .map(Option::getId)
                .collect(Collectors.toSet());
    }
}