package ua.edu.internship.interview.service.mapper;

import org.bson.types.ObjectId;

public interface BaseMapper {
    default String map(ObjectId id) {
        return id.toString();
    }
}
