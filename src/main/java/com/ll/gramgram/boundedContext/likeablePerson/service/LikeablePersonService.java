package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;
    private final Rq rq;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if (member.hasConnectedInstaMember() == false) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(member.getInstaMember()) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    public Optional<LikeablePerson> findById(Integer id){
        return likeablePersonRepository.findById(id);
    }
//    5차==>//4차 리팩토링 결과 ==> id가 존재한다면 지울 수있음 하지만 필요한건 id를 지울 수있는 권한이 로그인 한사람한테만 있어한다(이 부분에대해서 한번 더 생각해봐야한다)
//    1번 코드 강사님 수정 코드
    @Transactional
    public RsData<LikeablePerson> delete(Integer likeablePersonId) {
        Optional<LikeablePerson> optionalLikeablePerson = likeablePersonRepository.findById(likeablePersonId);
        if (!optionalLikeablePerson.isPresent()) {
            return RsData.of("F-3", "호감대상이 존재하지 않습니다.");
        }

        LikeablePerson likeablePerson = optionalLikeablePerson.get();
        likeablePersonRepository.delete(likeablePerson);
        return RsData.of("S-3", "삭제 완료!!");
    }
//    //2번코드
//    @Transactional
//    public RsData<LikeablePerson> delete(Integer likeablePersonId) {
//        Optional<LikeablePerson> optionalLikeablePerson = likeablePersonRepository.findById(likeablePersonId);
//        if (!optionalLikeablePerson.isPresent()) {
//            return RsData.of("F-3", "호감대상이 존재하지 않습니다.");
//        }
//        //권한 부여
//        LikeablePerson likeablePerson = optionalLikeablePerson.get();
//        if (!rq.getMember().getInstaMember().getId().equals(likeablePerson.getFromInstaMember().getId())) {
//            return RsData.of("F-4", "권한이 없습니다.");
//        }
//        likeablePersonRepository.delete(likeablePerson);
//        return RsData.of("S-3", "삭제 완료!!");
//    }

    //3번코드 ==> 굳이 잡자면 1번코드의 방식은 컨트롤러가 if문을 통해 권한을 체크하고 서비스에서 딜리트 메서드를 받아온다면 3번 코드는 아예 서비스에서 모든걸 다해서 내보내는형태이다.
    //즉 서비스에서 맡은 비중이 더 크기 때문에 1번방식을 선호한다.
//    @Transactional
//    public RsData<LikeablePerson> delete(Member member, Integer likeablePersonId) {
//        Optional<LikeablePerson> optionalLikeablePerson = likeablePersonRepository.findById(likeablePersonId);
//        if (!optionalLikeablePerson.isPresent()) {
//            return RsData.of("F-3", "호감대상이 존재하지 않습니다.");
//        }
//        //권한 부여
//        LikeablePerson likeablePerson = optionalLikeablePerson.get();
//        if (member.getInstaMember().getId().equals(likeablePerson.getFromInstaMember().getId())) {
//            return RsData.of("F-4", "권한이 없습니다.");
//        }
//        likeablePersonRepository.delete(likeablePerson);
//        return RsData.of("S-3", "삭제 완료!!");
//    }

}
