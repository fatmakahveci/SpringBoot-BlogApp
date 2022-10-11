// package com.fatmakahveci.blog;

// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.util.List;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.test.annotation.Rollback;

// import com.fatmakahveci.blog.model.Tag;
// // import com.fatmakahveci.blog.service.TagService;

// @DataJpaTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @Rollback(false)
// public class TagServiceTest {
//     // private TagService tagService;
//     Tag tag;

//     @BeforeEach
//     void setUp() {
//         tag = new Tag();
//         tag.setName("First Tag");
//     }
//     @Test
//     public void test() {
//         assertTrue(true);
//     }

//     @Test
//     public List<Tag> findAll() {
//         return null;
//     }

//     @Test
//     public Tag findById(Integer id) throws TagNotFoundException {
//         return null;
//     }

//     @Test
//     public Tag save(Tag tag) {
//         return null;
//     }

//     @Test
//     public Tag findTagByName(String name) {
//         return null;
//     }

//     @Test
//     public Tag getOrCreateTagByName(String name) {
//         return null;
//     }
// }
