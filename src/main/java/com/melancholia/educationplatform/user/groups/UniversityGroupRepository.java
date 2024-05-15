package com.melancholia.educationplatform.user.groups;

import com.melancholia.educationplatform.course.step.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityGroupRepository extends JpaRepository<UniversityGroup, Long> {
}
