-- Product Review
CREATE TABLE product_review (
    review_id        INT AUTO_INCREMENT PRIMARY KEY,
    product_id       INT NOT NULL,
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    title            VARCHAR(255) NOT NULL COMMENT '리뷰제목',
    content          TEXT NOT NULL COMMENT '리뷰내용',
    rating           INT NOT NULL COMMENT '별점',
    like_count       INT DEFAULT 0 COMMENT '좋아요',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(20) NULL COMMENT '등록자',
    updated_at       DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    updated_by       VARCHAR(20) DEFAULT NULL COMMENT '수정자',
    deleted_at       DATETIME DEFAULT NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(20) DEFAULT NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부(Y/N)',
    FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE,
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '상품리뷰';

-- Product Review Like
CREATE TABLE product_review_like (
    like_id          INT AUTO_INCREMENT PRIMARY KEY,
    review_id        INT NOT NULL COMMENT '리뷰ID',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (review_id) REFERENCES product_review (review_id) ON DELETE CASCADE,
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    UNIQUE KEY uq_review_like (review_id, member_code)
) COMMENT '상품리뷰 좋아요';
