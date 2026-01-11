package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public boolean existsByEmailIgnoreCase(String email);

    public User findByEmailIgnoreCase(String email);
}

