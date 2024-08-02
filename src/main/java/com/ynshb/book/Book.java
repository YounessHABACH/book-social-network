package com.ynshb.book;

import com.ynshb.common.BaseEntity;
import com.ynshb.feedback.Feedback;
import com.ynshb.history.BookTransactionHistory;
import com.ynshb.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Book extends BaseEntity {

    private String title;
    private String author;
    private String isbn;
    private String synopsis;
    private String cover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    List<BookTransactionHistory> histories;

    @Transient
    public double getRate() {
        if (Objects.isNull(feedbacks) || feedbacks.isEmpty()) {
            return 0;
        }
        return feedbacks.stream().mapToDouble(Feedback::getNote).average().orElse(0.0);
    }

}
