package com.petko.entities;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Table;

@javax.persistence.Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "books")
public class BooksEntity extends Entity {
    private int bookId;
    private String title;
    private String author;
    private Boolean isBusy;

    /**
     * @return bookId
     */
    @Id
    @Column(name = "bid", nullable = false, unique = true)
    public int getBookId() {
        return bookId;
    }

    /**
     * Sets bookId
     * @param bid - bookId
     */
    public void setBookId(int bid) {
        this.bookId = bid;
    }

    /**
     * @return title
     */
    @Basic
    @Column(name = "title", nullable = true, length = 50)
    public String getTitle() {
        return title;
    }

    /**
     * Sets title
     * @param title - title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return author
     */
    @Basic
    @Column(name = "author", nullable = true, length = 20)
    public String getAuthor() {
        return author;
    }

    /**
     * Sets author
     * @param author - author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return busy status
     */
    @Basic
    @Type(type = "yes_no")
    @Column(name = "isbusy", nullable = true)
    public Boolean getIsBusy() {
        return isBusy;
    }

    /**
     * Sets busy status
     * @param isbusy - isBusy
     */
    public void setIsBusy(Boolean isbusy) {
        this.isBusy = isbusy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BooksEntity that = (BooksEntity) o;

        if (bookId != that.bookId) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (author != null ? !author.equals(that.author) : that.author != null) return false;
        if (isBusy != null ? !isBusy.equals(that.isBusy) : that.isBusy != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bookId;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (isBusy != null ? isBusy.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Book [bookId=%d, title=%s, author=%s, isBusy=%b]", bookId, title, author, isBusy);
    }
}
