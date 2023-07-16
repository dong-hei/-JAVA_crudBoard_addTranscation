package board_dk.board.controller;

import board_dk.board.mapper.UserMapper;
import board_dk.board.model.Board;
import board_dk.board.model.QUser;
import board_dk.board.model.User;
import board_dk.board.repository.UserRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserApiController {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/users")
    Iterable<User> all(@RequestParam(required = false) String method, @RequestParam(required = false) String text){
        Iterable<User> users =null;
        if("query".equals(method)){
            users = repository.findByUsernameQuery(text);
        }else if("nativeQuery".equals(method)){
            users = repository.findByUsernameNativeQuery(text);
        }
        else if("querydsl".equals(method)){
            QUser user = QUser.user;
            Predicate predicate = user.username.contains(text);
            users = repository.findAll(predicate);

        }    else if("querydslCustom".equals(method)){
            users = repository.findByUsernameCustom(text);
        }   else if("jdbc".equals(method)){
            users = repository.findByUsernameJdbc(text);
        }   else if("mybatis".equals(method)){
            users = userMapper.getUsers(text);
        }
        else{
            users= repository.findAll();
        }
        return users;
    }

    @PostMapping("/users")
    User newUser(@RequestBody User newUser) {
        return repository.save(newUser);
    }

    // Single item

    @GetMapping("/users/{id}")
    User one(@PathVariable Long id) {

        return repository.findById(id).orElse(null);
    }

    @PutMapping("/users/{id}")
    User replaceUser(@RequestBody User newUser, @PathVariable Long id) {

        return repository.findById(id)
                .map(user -> {
//                    user.setTitle(newUser.getTitle());
//                    user.setContent(newUser.getContent());
//                    user.setBoards(newUser.getBoards());
                    user.getBoards().clear();
                    user.getBoards().addAll(newUser.getBoards());
                    for(Board board : user.getBoards()){
                        board.setUser(user);
                    }
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return repository.save(newUser);
                });
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
