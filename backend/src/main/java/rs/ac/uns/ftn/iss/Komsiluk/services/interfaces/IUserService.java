package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;

public interface IUserService {

	public User findById(Long id);

    public Collection<User> findAll();

    public User save(User user);
}
