package com.bookstore.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity
public class Author implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String genre;
    private int age;

    @Version
    private short version;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "author", orphanRemoval = true)
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        this.books.add(book);
        book.setAuthor(this);
    }

    public void removeBook(Book book) {
        book.setAuthor(null);
        this.books.remove(book);
    }

    public void removeBooks() {
        Iterator<Book> iterator = this.books.iterator();

        while (iterator.hasNext()) {
            Book book = iterator.next();

            book.setAuthor(null);
            iterator.remove();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public short getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Author{" + "id=" + id + ", name=" + name
                + ", genre=" + genre + ", age=" + age + '}';
    }

}


/*How To Batch Deletes In MySQL Via orphanRemoval=true

Description: Batch deletes in MySQL via orphanRemoval=true.

Note: Spring deleteAllInBatch() and deleteInBatch() don't use batching, don't take advantage of optimistic locking to prevent lost updates and don't take advantage of orphanRemoval=true. They trigger bulk operations and the Persistent Context is not synchronized accordingly (it is advisable to flush (before delete) and close/clear (after delete) the Persistent Context accordingly to avoid issues created by unflushed (if any) or outdated (if any) entities). The first one simply triggers a delete from entity_name statement, while the second one triggers a delete from entity_name where id=? or id=? or id=? ... statement. Using these methods for deleting parent-entities and the associated entites (child-entities) requires explicit calls for both sides. For batching rely on deleteAll(), deleteAll(Iterable<? extends T> entities) or even better, on delete() method. Behind the scene, deleteAll() methods uses delete(). The delete() and deleteAll() methods rely on EntityManager.remove(), therefore, the Persistent Context is synchronized. Moreover, cascading removals and orphanRemoval are taken into account.

Key points for using deleteAll()/delete():

in this example, we have a Author entity and each author can have several Book (one-to-many)
first, we use orphanRemoval=true and CascadeType.ALL
second, we dissociate all Book from the corresponding Author
third, we explicitly (manually) flush the Persistent Context; is time for orphanRemoval=true to enter into the scene; thanks to this setting, all disassociated books will be deleted; the generated DELETE statements are batched (if orphanRemoval is set to false, a bunch of updates will be executed instead of deletes)
forth, we delete all Author via the deleteAll() or delete() method (since we have dissaciated all Book, the Author deletion will take advantage of batching as well)
 * 
*/