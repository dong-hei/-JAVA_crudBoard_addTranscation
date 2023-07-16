package board_dk.board.service;

import board_dk.board.model.Board;
import board_dk.board.model.Role;
import board_dk.board.model.User;
import board_dk.board.repository.BoardRepository;
import board_dk.board.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    트랜젝션 어노테이션만으로 트랜젝션이 선언된다는 보장은 없다
//    괄호를 써서 속성값을 정의할수도 있다.
//    다른 클래스의 메소드를 정의할경우 클래스 첫줄부터 트랜잭션을 시작할지 다른클래스의 메소드부터 트랜잭션을 시작할지
//    (propagation = ) 으로 설정할수있다. (자주 사용하진 않음)
//    required (default) 다른 클래스에서 트랜잭션이 있다면 그걸 먼저 실행하고, 다른 클래스의 트랜잭션이 없으면 기존의 트랜잭션부터 시작
//    (isolation = ) 여러개의 트랜젝션이 실행될때 각각의 트랜잭션이 다른 트랜잭션의 연산에 영향을 받지않도록 보장한다.
//    외부클래스의 메소드를 호출해야 트랜잭션이 적용된다 (동일한클래스의 메소드,퍼블릭이여야한다.)
//    (rollbackFor = { XXX.class }) XXX.class를 롤백시킨다.
    @Transactional
    public User save(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);

        Role role = new Role();
        role.setId(1l);
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);

        // 사용자 가입인사글 자동작성, 트랜잭션 처리를 하지 않으면 에러가 나도 자동으로 작성된다
        // 가입인사글 자동작성이면 괜찮은데 만약 포인트적립같은 중요한 내용이면 문제가 된다.
        // 가입은 했는데 포인트적립이 되지않는다면? 문제발생

        Board board = new Board();
        board.setTitle("안녕하세요!");
        board.setContent("반갑습니다");
        board.setUser(savedUser);
        boardRepository.save(board);

        return savedUser;
    }

}
