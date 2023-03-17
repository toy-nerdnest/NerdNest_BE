package com.server.domain.member.mapper;

import com.server.domain.member.dto.MemberDto;
import com.server.domain.member.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    Member memberPostDtoToMember(MemberDto.Post post);
    Member memberPatchDtoToMember(MemberDto.Patch patch);

    MemberDto.Response memberToMemberResponseDto(Member member);
}
