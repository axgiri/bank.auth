package github.axgiri.bankauthentication.service;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import github.axgiri.bankauthentication.dto.request.LoginRequest;
import github.axgiri.bankauthentication.dto.request.PersonRequest;
import github.axgiri.bankauthentication.dto.response.AuthResponse;
import github.axgiri.bankauthentication.dto.response.PersonResponse;
import github.axgiri.bankauthentication.entity.Person;
import github.axgiri.bankauthentication.exception.InvalidTokenException;
import github.axgiri.bankauthentication.exception.UserNotFoundException;
import github.axgiri.bankauthentication.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    
    private final PersonRepository repository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Qualifier("asyncExecutor")
    private final TaskExecutor taskExecutor;

    public PersonResponse create(PersonRequest personRequest) {
        log.info("creating person with first name: {}", personRequest.getFirstName());
        return PersonResponse.fromEntityToDto(repository.save(personRequest.toEntity()));
    }

    public void createAsync(PersonRequest personRequest) {
        log.info("creating person with first name: {}", personRequest.getFirstName());
        taskExecutor.execute(() -> {
            repository.save(personRequest.toEntity());
            log.info("created person with first name: {}", personRequest.getFirstName());
        });
    }

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword()));
        var person = repository.findByPhoneNumber(request.getPhoneNumber())
            .orElseThrow(() -> new UserNotFoundException("user not found with phone number: " + request.getPhoneNumber()));
        CompletableFuture<String> token = tokenService.generateToken(person);
        return new AuthResponse(token.join(), PersonResponse.fromEntityToDto(person));
    }

    public PersonResponse findById(Long id) {
        log.info("finding person with id: {}", id);
        return PersonResponse.fromEntityToDto(repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("person not found with id: " + id)));
    }

    public PersonResponse findByPhoneNumber(String phoneNumber) {
        log.info("finding person with phone number: {}", phoneNumber);
        return PersonResponse.fromEntityToDto(repository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("person not found with phone number: " + phoneNumber))
        );
    }

    @Async("asyncExecutor")
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public CompletableFuture<PersonResponse> update(Long id, PersonRequest personRequest) {
        log.info("updating person with id: {}", id);
        return CompletableFuture.supplyAsync(() -> {
            Person person = repository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("person not found with id: " + id));
                    
            person.setFirstName(personRequest.getFirstName());
            person.setLastName(personRequest.getLastName());
            person.setPhoneNumber(personRequest.getPhoneNumber());
            person.setEmail(personRequest.getEmail());
            person.setUpdatedAt(LocalDate.now());
            return PersonResponse.fromEntityToDto(repository.save(person));
        }, taskExecutor);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) {
        log.info("deleting person with id: {}", id);
        Person person = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("person not found with id: " + id));
        repository.delete(person);
        log.info("deleted person with id: {}", id);
    }

    public void validateToken(String token) {
        log.info("validating token: {}", token);
        if (!tokenService.isTokenValid(token,
                repository.findByPhoneNumber(tokenService.extractUsername(token)).orElseThrow(
                        () -> new UserNotFoundException("person not found")))) {
            throw new InvalidTokenException("token is invalid");
        }
    }
}
