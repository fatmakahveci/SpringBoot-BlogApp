package com.fatmakahveci.blog;

import static org.junit.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatmakahveci.blog.dao.TagRepository;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.impl.TagServiceImpl;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    public void saveTagSuccess() throws Exception {
        Tag newTag = new Tag(null, "tag", Collections.emptySet());
        Tag savedTag = new Tag(1, "tag", Collections.emptySet());

        when(tagRepository.save(eq(newTag))).thenReturn(savedTag);
        Tag returnedTag = tagService.save(newTag);

        verify(tagRepository, times(1)).save(eq(newTag));
        assertNotNull(returnedTag.getId());

        assertThat(returnedTag).isEqualTo(savedTag);
    }

    @Test
    public void deleteTagSuccess() {
        Tag tag = new Tag(1, "tag", Collections.emptySet());
        tagRepository.save(tag);
        tagRepository.deleteById(tag.getId());
        Optional<Tag> tagOptional = tagRepository.findById(tag.getId());
        assertThat(tagOptional).isEmpty();
    }

}
