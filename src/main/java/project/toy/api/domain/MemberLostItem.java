package project.toy.api.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class MemberLostItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_LOST_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Enumerated(EnumType.STRING)
    private LostCategory category;       // 분실물 종류

    private String itemName;             // 분실물명

    private String itemDetailInfo;      // 분실물 상세내용

    @ColumnDefault("'N'")
    private String sendStatus;

    @Builder
    public MemberLostItem(Member member, LostCategory category, String itemName, String itemDetailInfo, String sendStatus) {
        this.member = member;
        if (!member.getMemberLostItems().contains(this)) {
            member.getMemberLostItems().add(this);
        }
        this.category = category;
        this.itemName = itemName;
        this.itemDetailInfo = itemDetailInfo;
        this.sendStatus = sendStatus != this.sendStatus ? this.sendStatus : "N";
    }

    public void edit(LostCategory category, String itemName, String itemDetailInfo) {
        this.category = category != null ? category : this.category;
        this.itemName = itemName != null ? itemName : this.itemName;
        this.itemDetailInfo = itemDetailInfo != null ? itemDetailInfo : this.itemDetailInfo;
    }

    @Override
    public String toString() {
        return "MemberLostItem{" +
                "id=" + id +
                ", category=" + category +
                ", itemName='" + itemName + '\'' +
                ", itemDetailInfo='" + itemDetailInfo + '\'' +
                ", sendStatus='" + sendStatus + '\'' +
                '}';
    }
}
