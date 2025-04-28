package com.example.robotmanagement.repository;

import com.example.robotmanagement.entity.Robot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RobotRepository extends JpaRepository<Robot, Long> {
    List<Robot> findByOwnerId(Long ownerId);

    @Query("SELECT r.id FROM Robot r WHERE r.owner.id = :ownerId")
    List<Long> findRobotIdsByOwnerId(Long ownerId);

    @Query("SELECT r.owner.id FROM Robot r WHERE r.id = :robotId")
    Long findUserIdByRobotId(@Param("robotId") Long robotId);

}
