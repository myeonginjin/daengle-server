package ddog.persistence.rdb.adapter;

import ddog.domain.review.CareReview;
import ddog.persistence.rdb.jpa.entity.CareReviewJpaEntity;
import ddog.persistence.rdb.jpa.repository.CareReviewJpaRepository;
import ddog.domain.review.port.CareReviewPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CareReviewRepository implements CareReviewPersist {

    private final CareReviewJpaRepository careReviewJpaRepository;

    @Override
    public Optional<CareReview> findByReviewId(Long careReviewId) {
        return careReviewJpaRepository.findById(careReviewId).map(CareReviewJpaEntity::toModel);
    }

    @Override
    public CareReview save(CareReview careReview) {
        CareReviewJpaEntity careReviewJpaEntity = careReviewJpaRepository.save(CareReviewJpaEntity.from(careReview));
        return careReviewJpaEntity.toModel();
    }

    @Override
    public void delete(CareReview careReview) {
        careReviewJpaRepository.delete(CareReviewJpaEntity.from(careReview));
    }

    @Override
    public Optional<CareReview> findByReservationId(Long reservationId) {
        return careReviewJpaRepository.findByReservationId(reservationId).map(CareReviewJpaEntity::toModel);
    }

    @Override
    public Optional<CareReview> findByReviewerIdAndReservationId(Long reviewerId, Long reservationId) {
        return careReviewJpaRepository.findByReviewerIdAndReservationId(reviewerId, reservationId).map(CareReviewJpaEntity::toModel);
    }

    @Override
    public Page<CareReview> findByReviewerId(Long reviewerId, Pageable pageable) {
        return careReviewJpaRepository.findByReviewerId(reviewerId, pageable).map(CareReviewJpaEntity::toModel);
    }

    @Override
    public Page<CareReview> findByRevieweeId(Long userId, Pageable pageable) {
        return careReviewJpaRepository.findByRevieweeId(userId, pageable).map(CareReviewJpaEntity::toModel);
    }

    @Override
    public Page<CareReview> findByVetId(Long vetId, Pageable pageable) {
        return careReviewJpaRepository.findByVetId(vetId, pageable).map(CareReviewJpaEntity::toModel);
    }
}

