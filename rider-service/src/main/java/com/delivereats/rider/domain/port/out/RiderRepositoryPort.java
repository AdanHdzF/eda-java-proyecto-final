package com.delivereats.rider.domain.port.out;

import com.delivereats.rider.domain.model.Rider;
import com.delivereats.rider.domain.model.RiderStatus;

import java.util.Optional;

public interface RiderRepositoryPort {
    void save(Rider rider);
    Optional<Rider> findAvailable();
    void updateStatus(String riderId, RiderStatus status);
}
