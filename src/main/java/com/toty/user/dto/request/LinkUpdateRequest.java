package com.toty.user.dto.request;

import com.toty.user.dto.response.LinkDto;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LinkUpdateRequest {
    private List<LinkDto> links; // nullable
}
