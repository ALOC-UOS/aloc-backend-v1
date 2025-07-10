package com.aloc.aloc.algorithm.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
public class AlgorithmServiceTest {

    // Algorithm Repository를 모방한 가짜 객체 만들기
    @Mock
    private AlgorithmRepository algorithmRepository;

    // 가짜 객체를 사용하는 AlgorithmService 객체 만들기
    @InjectMocks
    private AlgorithmService algorithmService;

    @Test
    void getAlgorithmsByIds() {
        //given
        //id 1인 객체를 생성
        Integer algorithmId = 1;
        Algorithm algorithm = Algorithm.builder()
            .algorithmId(algorithmId)
            .koreanName("테스트")
            .englishName("test")
            .build();
        //algorithmRepository에서 algorithmId 1인 객체를 조회하면 algorithm 객체를 반환
        given(algorithmRepository.findByAlgorithmId(algorithmId)).willReturn(Optional.of(algorithm));
    
        //when
        //algorithmId를 List.of(algorithmId)로 넘겨서 조회하고 반환된 객체를 List<Algorithm> result에 저장
        List<Algorithm> result = algorithmService.getAlgorithmsByIds(List.of(algorithmId));

        //then
        //result의 크기가 1이고, 첫 번째 요소의 algorithmId가 1인지 확인
        assertThat(result).containsExactly(algorithm);
    }
}   
