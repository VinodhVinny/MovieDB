package org.ragnarok.movieDB.repository;

import org.ragnarok.movieDB.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
