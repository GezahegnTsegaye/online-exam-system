package et.gov.online_exam.repository;

import org.springframework.data.repository.CrudRepository;

import et.gov.online_exam.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByUsername(String username);

}
