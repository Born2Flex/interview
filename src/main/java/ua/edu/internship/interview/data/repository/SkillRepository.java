package ua.edu.internship.interview.data.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.edu.internship.interview.data.entity.SkillEntity;
import ua.edu.internship.interview.service.dto.SkillTreeDto;

import java.util.List;

public interface SkillRepository extends MongoRepository<SkillEntity, String> {
    @Aggregation(pipeline = {
            """
            {
              $match: {
                  parentId: null
              }
            }
            """,
            """
            {
              $lookup: {
                from: "skills",
                  let: {
                    parentId: "$_id"
                  },
                  pipeline: [
                    {
                      $match: {
                        $expr: {
                          $eq: ["$$parentId", "$parentId"]
                        }
                      }
                    },
                    {
                      $lookup: {
                        from: "skills",
                        localField: "_id",
                        foreignField: "parentId",
                        as: "children"
                      }
                    }
                  ],
                as: "children"
              }
            }
            """})
    List<SkillTreeDto> findSkillTree();
}
