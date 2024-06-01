package mk.ukim.finki.wp.jan2023.service.impl;

import mk.ukim.finki.wp.jan2023.model.Candidate;
import mk.ukim.finki.wp.jan2023.model.Gender;
import mk.ukim.finki.wp.jan2023.model.Party;
import mk.ukim.finki.wp.jan2023.model.exceptions.InvalidCandidateIdException;
import mk.ukim.finki.wp.jan2023.model.exceptions.InvalidPartyIdException;
import mk.ukim.finki.wp.jan2023.repository.CandidateRepository;
import mk.ukim.finki.wp.jan2023.repository.PartyRepository;
import mk.ukim.finki.wp.jan2023.service.CandidateService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final PartyRepository partyRepository;

    public CandidateServiceImpl(CandidateRepository candidateRepository, PartyRepository partyRepository) {
        this.candidateRepository = candidateRepository;
        this.partyRepository = partyRepository;
    }

    @Override
    public List<Candidate> listAllCandidates() {
        return candidateRepository.findAll();
    }

    @Override
    public Candidate findById(Long id) {
        return candidateRepository.findById(id).orElseThrow(InvalidCandidateIdException::new);
    }

    @Override
    public Candidate create(String name, String bio, LocalDate dateOfBirth, Gender gender, Long party) {
        Party p = partyRepository.findById(party).orElseThrow(InvalidPartyIdException::new);
        return candidateRepository.save(new Candidate(name,bio,dateOfBirth,gender,p));
    }

    @Override
    public Candidate update(Long id, String name, String bio, LocalDate dateOfBirth, Gender gender, Long party) {
        Party p = partyRepository.findById(party).orElseThrow(InvalidPartyIdException::new);
        Candidate c = findById(id);
        c.setBio(bio);
        c.setGender(gender);
        c.setName(name);
        c.setDateOfBirth(dateOfBirth);
        c.setParty(p);
        return candidateRepository.save(c);
    }

    @Override
    public Candidate delete(Long id) {
        Candidate c = findById(id);
        candidateRepository.delete(c);
        return c;
    }

    @Override
    public Candidate vote(Long id) {
        Candidate c = findById(id);
        c.setVotes(c.getVotes()+1);
        return candidateRepository.save(c);
    }

    @Override
    public List<Candidate> listCandidatesYearsMoreThanAndGender(Integer yearsMoreThan, Gender gender) {
        if(yearsMoreThan!=null && gender!=null)
        {
            LocalDate date=LocalDate.now().minusYears(yearsMoreThan);
            return candidateRepository.findAllByGenderAndDateOfBirthIsBefore(gender,date);
        }
        else if(gender!=null)
        {
            return candidateRepository.findAllByGender(gender);
        }
        else if(yearsMoreThan!=null)
        {
            LocalDate date=LocalDate.now().minusYears(yearsMoreThan);
            return candidateRepository.findByDateOfBirthIsBefore(date);
        }
        return candidateRepository.findAll();
    }
}
