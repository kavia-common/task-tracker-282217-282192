package com.example.todobackend.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * PUBLIC_INTERFACE
 * Repository for Todo entities.
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {

    /**
     * PUBLIC_INTERFACE
     * Find todos filtered by completion status and simple search on title/description.
     */
    @Query("""
            select t from Todo t
            where (:completed is null or t.completed = :completed)
              and (:q is null or lower(t.title) like lower(concat('%', :q, '%')) 
                           or lower(t.description) like lower(concat('%', :q, '%')))
            order by t.createdAt desc
            """)
    List<Todo> findFiltered(Boolean completed, String q);
}
