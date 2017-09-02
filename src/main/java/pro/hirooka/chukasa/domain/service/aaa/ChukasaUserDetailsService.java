package pro.hirooka.chukasa.domain.service.aaa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.entity.aaa.OperationEntity;
import pro.hirooka.chukasa.domain.entity.aaa.PermissionEntity;
import pro.hirooka.chukasa.domain.entity.aaa.RoleEntity;
import pro.hirooka.chukasa.domain.entity.aaa.UserDetailsEntity;
import pro.hirooka.chukasa.domain.entity.aaa.type.OperationType;
import pro.hirooka.chukasa.domain.repository.aaa.OperationEntityRepository;
import pro.hirooka.chukasa.domain.repository.aaa.PermissionEntityRepository;
import pro.hirooka.chukasa.domain.repository.aaa.RoleEntityRepository;
import pro.hirooka.chukasa.domain.repository.aaa.UserDetailsEntityRepository;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Service
public class ChukasaUserDetailsService implements IChukasaUserDetailsService {

    @Autowired
    UserDetailsEntityRepository repository;

    // TODO: delete
    @Autowired
    RoleEntityRepository roleEntityRepository;
    @Autowired
    PermissionEntityRepository permissionEntityRepository;
    @Autowired
    OperationEntityRepository operationEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findOneByUsername(username);
    }

    @PostConstruct
    void init(){

        // TODO: example data
        OperationEntity operationEntity = new OperationEntity();
        operationEntity.setOperationType(OperationType.READ);
        operationEntity.setName(OperationType.READ.name());
        operationEntityRepository.save(operationEntity);
        operationEntity = new OperationEntity();
        operationEntity.setOperationType(OperationType.WRITE);
        operationEntity.setName(OperationType.WRITE.name());
        operationEntityRepository.save(operationEntity);

        Set<OperationEntity> operationEntitySet = new HashSet<>();
        operationEntitySet.addAll(operationEntityRepository.findAll());
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setName("ALL");
        permissionEntityRepository.save(permissionEntity);
        operationEntitySet = new HashSet<>();
        operationEntitySet.add(operationEntityRepository.findOneByName(OperationType.READ.name()));
        permissionEntity = new PermissionEntity();
        permissionEntity.setName("READ_ONLY");
        permissionEntityRepository.save(permissionEntity);

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ADMIN");
        roleEntity.setAuthority("ROLE_ADMIN");
        Set<PermissionEntity> permissionEntitySet = new HashSet<>();
        permissionEntitySet.add(permissionEntityRepository.findOneByName("ALL"));
        roleEntity.setPermissionEntitySet(permissionEntitySet);
        roleEntity = roleEntityRepository.save(roleEntity);
        Set<RoleEntity> roleEntitySet = new HashSet<>();
        roleEntitySet.add(roleEntity);
        UserDetailsEntity userDetailsEntity = new UserDetailsEntity();
        userDetailsEntity.setUsername("admin");
        userDetailsEntity.setPassword("admin");
        userDetailsEntity.setRoleEntitySet(roleEntitySet);
        repository.save(userDetailsEntity);

        roleEntity = new RoleEntity();
        roleEntity.setName("GUEST");
        roleEntity.setAuthority("ROLE_GUEST");
        permissionEntitySet = new HashSet<>();
        permissionEntitySet.add(permissionEntityRepository.findOneByName("READ_ONLY"));
        roleEntity.setPermissionEntitySet(permissionEntitySet);
        roleEntity = roleEntityRepository.save(roleEntity);
        roleEntitySet = new HashSet<>();
        roleEntitySet.add(roleEntity);
        userDetailsEntity = new UserDetailsEntity();
        userDetailsEntity.setUsername("guest");
        userDetailsEntity.setPassword("guest");
        userDetailsEntity.setRoleEntitySet(roleEntitySet);
        repository.save(userDetailsEntity);
    }
}
