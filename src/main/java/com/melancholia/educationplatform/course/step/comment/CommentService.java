package com.melancholia.educationplatform.course.step.comment;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public void commentSave(Comment comment){
        commentRepository.save(comment);
    }

}
