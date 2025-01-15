package com.example.userservice.Service;

import com.example.userservice.Client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment environment;
    private final OrderServiceClient orderServiceClient;
//    private final RestTemplate restTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findAllByEmail(username);
        if(user.isPresent()){
            return new User(user.get().getEmail(), user.get().getEncryptedPwd(),
                    true, true,true, true, new ArrayList<>());
        }else {
            throw new UsernameNotFoundException("User not found : "+username);
        }

    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(bCryptPasswordEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);
        // 바로 반환
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        Optional<UserEntity> userEntity= userRepository.findByUserId(userId);

        if(userEntity.isPresent()){
            UserDto userDto = new ModelMapper().map(userEntity.get(),UserDto.class);
//            List<ResponseOrder> orders = new ArrayList<>();
//            Using as rest template
//            String orderUrl ="http://127.0.0.1:8000/order-service/%s/orders";
//            String orderUrl = String.format(environment.getProperty("order-service.url"),userId);
//            ResponseEntity<List<ResponseOrder>> orderListResponse =
//                    restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                    new ParameterizedTypeReference<List<ResponseOrder>>() {
//            });
//            List<ResponseOrder> orders = orderListResponse.getBody();

//            Using as feign client
//            Feign Exception handling
//            List<ResponseOrder> orders = null;
//            try {
//                orders = orderServiceClient.getOrders(userId);
//            }catch (FeignException e){
//                log.error(e.getMessage());
//            }

            List<ResponseOrder> orders =orderServiceClient.getOrders(userId);
            userDto.setOrders(orders);
            return userDto;

        }else{
            throw new UsernameNotFoundException("User not found");
        }

    }

    @Override
    public Iterable<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        Optional<UserEntity> user= userRepository.findByEmail(email);
        if(user.isPresent()){
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            UserDto userDto = modelMapper.map(user.get(),UserDto.class);
            return userDto;
        }else {
            throw new UsernameNotFoundException("email not found");
        }
    }

}
