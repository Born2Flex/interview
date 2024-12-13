package ua.edu.internship.interview.service.mapper;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BaseMapperTest {
    private BaseMapper underTest;

    @BeforeEach
    public void setUp() {
        underTest = new BaseMapper();
    }

    @Test
    void testMap() {
        ObjectId objectId = new ObjectId();

        String result = underTest.map(objectId);

        assertNotNull(result);
        assertEquals(objectId.toString(), result);
    }
}
