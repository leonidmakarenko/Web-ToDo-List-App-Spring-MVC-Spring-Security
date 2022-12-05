package leo.springwebapp.spring_web_todolist.repository;

import leo.springwebapp.spring_web_todolist.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}