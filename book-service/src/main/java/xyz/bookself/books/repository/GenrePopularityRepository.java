package xyz.bookself.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xyz.bookself.books.domain.GenrePopularity;

import javax.transaction.Transactional;
import java.util.Collection;

public interface GenrePopularityRepository extends JpaRepository<GenrePopularity, Integer> {
    @Query(nativeQuery = true, value = "SELECT * FROM popular_books_by_genre WHERE genre = ?1 ORDER BY rank LIMIT ?2")
    Collection<GenrePopularity> getPopularBooksByGenre(String genre, int limit);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "DELETE FROM popular_books_by_genre WHERE 1 = 1")
    void clean();
}
