package com.fatmakahveci.blog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        when(tagRepository.save(newTag)).thenReturn(savedTag);

        Tag returnedTag = tagService.save(newTag);
        verify(tagRepository, times(1)).save(newTag);

        assertNotNull(returnedTag.getId());
        assertThat(returnedTag).isEqualTo(savedTag);
    }

    @Test
    public void findAllSuccess() {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag(1, "first tag", Collections.emptySet()));
        tags.add(new Tag(2, "second tag", Collections.emptySet()));

        when(tagRepository.findAll()).thenReturn(tags);

        List<Tag> actualTags = tagService.findAll();
        verify(tagRepository, times(1)).findAll();

        assertEquals(tags, actualTags);
    }

    @Test
    public void findAllSuccess_emptyTagList() {
        List<Tag> tags = new ArrayList<>();

        when(tagRepository.findAll()).thenReturn(tags);

        List<Tag> actualTags = tagService.findAll();
        verify(tagRepository, times(1)).findAll();

        assertThat(actualTags).isEmpty();
    }

    @Test
    public void deleteByIdSuccess() {
        Tag tag = new Tag(1, "tag", Collections.emptySet());

        when(tagRepository.findById(1)).thenReturn(Optional.of(tag));

        Optional<Tag> deletedOptionalTag = tagService.deleteById(1);
        verify(tagRepository, times(1)).findById(1);
        verify(tagRepository, times(1)).deleteById(1);

        assertEquals(deletedOptionalTag.get(), tag);
    }

    @Test
    public void deleteByIdFail_notExistingTag() {
        when(tagRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Tag> deletedOptionalTag = tagService.deleteById(1);
        verify(tagRepository, times(1)).findById(1);
        verify(tagRepository, times(1)).deleteById(1);

        assertEquals(deletedOptionalTag, Optional.empty());
    }

    @Test
    public void findByIdSuccess() {
        Tag tag = new Tag(1, "tag", Collections.emptySet());

        when(tagRepository.findById(1)).thenReturn(Optional.of(tag));

        Optional<Tag> optionalTag = tagService.findById(1);
        verify(tagRepository, times(1)).findById(1);

        assertEquals(tag, optionalTag.get());
    }

    @Test
    public void findByIdFail() {
        when(tagRepository.findById(2)).thenReturn(Optional.empty());

        Optional<Tag> optionalTag = tagService.findById(2);
        verify(tagRepository, times(1)).findById(2);

        assertThat(optionalTag).isEmpty();
    }

    @Test
    public void findByNameSuccess() {
        Tag tag = new Tag(1, "tag", Collections.emptySet());

        when(tagRepository.findByName("tag")).thenReturn(Optional.of(tag));

        Optional<Tag> optionalTag = tagService.findByName("tag");
        verify(tagRepository, times(1)).findByName("tag");

        assertEquals("tag", optionalTag.get().getName());
    }

    @Test
    public void findByNameFail() {
        when(tagRepository.findByName("tag")).thenReturn(Optional.empty());

        Optional<Tag> optionalTag = tagService.findByName("tag");
        verify(tagRepository, times(1)).findByName("tag");

        assertThat(optionalTag).isEmpty();
    }

    @Test
    public void getOrCreateByName_nonExistingTag() {
        when(tagRepository.findByName("tag")).thenReturn(Optional.empty());

        Tag tag = tagService.getOrCreateByName("tag");
        verify(tagRepository, times(1)).findByName("tag");

        assertEquals(tag.getName(), "tag");
    }

    @Test
    public void getOrCreateByName_existingTag() {
        Tag existingTag = new Tag(1, "tag", Collections.emptySet());
        when(tagRepository.findByName("tag")).thenReturn(Optional.of(existingTag));

        Tag tag = tagService.getOrCreateByName("tag");
        verify(tagRepository, times(1)).findByName("tag");

        assertEquals(tag.getId(), existingTag.getId());
    }
}
