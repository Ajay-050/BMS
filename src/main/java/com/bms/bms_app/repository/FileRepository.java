package com.bms.bms_app.repository;

import com.bms.bms_app.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
