package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.CurrentlyReadingDTO;
import com.example.MangaWebSite.models.ReadingProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Integer> {
    Optional<ReadingProgress> findByChapter(Chapter chapter);

    // 1. Знайти останній прогрес для користувача по всіх коміксах,
    // де при завантаженні ми отримуємо і главу, і комікс (через главу)
    @Query("SELECT rp FROM ReadingProgress rp " +
            "JOIN FETCH rp.chapter ch " +
            "JOIN FETCH ch.comics c " +
            "WHERE rp.person.id = :personId " +
            "ORDER BY rp.updatedAt DESC")
    List<ReadingProgress> findRecentlyReadByPersonId(@Param("personId") int personId, Pageable pageable);

    // 2. Знайти прогрес за парою (person, comics), використовуючи зв’язок через главу
    @Query("SELECT rp FROM ReadingProgress rp " +
            "JOIN rp.chapter ch " +
            "WHERE rp.person.id = :personId AND ch.comics.id = :comicsId")
    Optional<ReadingProgress> findByPersonIdAndComicsId(@Param("personId") int personId,
                                                        @Param("comicsId") int comicsId);


    // 3. Знайти комікси, які зараз читають (тобто, де rp.updatedAt > :cutoffTime)
    // Групуючи за коміксом через главу
    @Query("""
           SELECT ch.comics 
           FROM ReadingProgress rp 
           JOIN rp.chapter ch 
           WHERE rp.updatedAt > :cutoffTime 
           GROUP BY ch.comics 
           ORDER BY COUNT(rp.person.id) DESC
           """)
    List<Comics> findCurrentlyReading(@Param("cutoffTime") LocalDateTime cutoffTime, Pageable pageable);

    // 4. Знайти популярні комікси (згідно з кількістю користувачів та рейтингом)
    @Query("""
           SELECT ch.comics 
           FROM ReadingProgress rp 
           JOIN rp.chapter ch 
           WHERE rp.updatedAt > :cutoffTime 
           GROUP BY ch.comics 
           ORDER BY COUNT(rp.person.id) DESC, ch.comics.popularityRating DESC
           """)
    Page<Comics> findCurrentlyPopularReading(@Param("cutoffTime") LocalDateTime cutoffTime, Pageable pageable);

    // 5. Знайти останній (найновіший) прогрес для даного коміксу та користувача
    @Query("SELECT rp FROM ReadingProgress rp " +
            "JOIN FETCH rp.chapter ch " +
            "JOIN FETCH ch.comics c " +
            "WHERE c.id = :comicsId " +
            "AND rp.person.id = :personId " +
            "ORDER BY rp.updatedAt DESC")
    Optional<ReadingProgress> findLatestByComicsIdAndPersonId(@Param("comicsId") int comicsId,
                                                              @Param("personId") int personId);

    Optional<ReadingProgress> findFirstByPersonIdAndChapter_Comics_IdOrderByUpdatedAtDesc(int personId, int comicsId);

    Optional<ReadingProgress> findByPersonIdAndChapterId(int personId, int chapterId);

    @Query("SELECT COUNT(DISTINCT rp) FROM ReadingProgress rp WHERE rp.person.id = :personId AND rp.isCompleted = true")
    int countCompletedChaptersByPerson(@Param("personId") int personId);

    @Query("SELECT COUNT(DISTINCT rp.chapter.comics.id) FROM ReadingProgress rp " +
            "WHERE rp.person.id = :personId AND rp.isCompleted = true")
    int countCompletedComicsByPerson(@Param("personId") int personId);

    @Query("SELECT COUNT(DISTINCT rp) FROM ReadingProgress rp " +
            "WHERE rp.person.id = :personId AND rp.chapter.comics.id = :comicId AND rp.isCompleted = true")
    int countCompletedChaptersByPersonAndComic(@Param("personId") int personId, @Param("comicId") int comicId);

    @Query(value = "SELECT to_char(rp.updated_at, 'YYYY-IW') as week, COUNT(DISTINCT rp.id) as count " +
            "FROM reading_progress rp WHERE rp.person_id = :personId AND rp.is_completed = true " +
            "AND rp.updated_at > :startDate GROUP BY week ORDER BY week", nativeQuery = true)
    Map<String, Integer> getWeeklyReadingStats(@Param("personId") int personId,
                                               @Param("startDate") LocalDateTime startDate);

    default Map<String, Integer> getWeeklyReadingStats(int personId) {
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(8);
        return getWeeklyReadingStats(personId, startDate);
    }

    @Query(value = "SELECT to_char(rp.updated_at, 'YYYY-MM') as month, COUNT(DISTINCT rp.id) as count " +
            "FROM reading_progress rp WHERE rp.person_id = :personId AND rp.is_completed = true " +
            "AND rp.updated_at > :startDate GROUP BY month ORDER BY month", nativeQuery = true)
    Map<String, Integer> getMonthlyReadingStats(@Param("personId") int personId,
                                                @Param("startDate") LocalDateTime startDate);

    default Map<String, Integer> getMonthlyReadingStats(int personId) {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(6);
        return getMonthlyReadingStats(personId, startDate);
    }
}
