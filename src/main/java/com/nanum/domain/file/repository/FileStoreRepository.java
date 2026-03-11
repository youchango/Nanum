package com.nanum.domain.file.repository;

import com.nanum.domain.file.model.FileStore;
import com.nanum.domain.file.model.ReferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileStoreRepository extends JpaRepository<FileStore, String> {

        List<FileStore> findByReferenceTypeAndReferenceIdOrderByDisplayOrderAsc(ReferenceType referenceType,
                        String referenceId);

        List<FileStore> findByReferenceTypeAndReferenceIdIn(ReferenceType referenceType, List<String> referenceIds);

        @Modifying
        @Query("UPDATE FileStore f SET f.isMain = :isMain WHERE f.referenceType = :referenceType AND f.referenceId = :referenceId")
        void updateMainStatus(@Param("referenceType") ReferenceType referenceType,
                        @Param("referenceId") String referenceId,
                        @Param("isMain") String isMain);

        @Modifying
        @Query("UPDATE FileStore f SET f.isMain = :isMain WHERE f.fileId = :fileId")
        void updateMainStatusById(@Param("fileId") String fileId, @Param("isMain") String isMain);
}
