package project.toy.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class MemberLostItemCreate {

    @NotBlank(message = "카테고리를 선택해주세요.")
    private String category;

    @NotBlank(message = "분실물 이름을 입력해주세요.")
    private String itemName;

    @NotBlank(message = "분실물 상세내용을 입력해주세요.")
    private String itemDetailInfo;

    @Builder
    public MemberLostItemCreate(String category, String itemName, String itemDetailInfo) {
        this.category = category;
        this.itemName = itemName;
        this.itemDetailInfo = itemDetailInfo;
    }
}
