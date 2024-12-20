package com.jxp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.sub_erupt.Layout;
import xyz.erupt.annotation.sub_erupt.Layout.FormSize;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.EditType;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.sub_edit.AttachmentType;
import xyz.erupt.annotation.sub_field.sub_edit.Search;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-20 14:43
 */
@Erupt(name = "商品品牌"
        , desc = "商品的描述"
        , orderBy = "PmsBrand.sort desc" // 字段排序：类名.字段名 [asc|desc], 类名.字段名 [asc|desc] ...
//        ,linkTree = @LinkTree(field = "category") //左树右表配置
        , layout = @Layout(
        // 固定前三列
        formSize = FormSize.FULL_LINE,
        // 使用前端分页
        pagingType = Layout.PagingType.BACKEND,
        // 每页显示20条数据
        pageSize = 20
)
)
@Table(name = "pms_brand")
@Entity
public class PmsBrand {

    // extends BaseModel
    @Id
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "native")
    @Column(name = "id")
    @EruptField
    private Long id;

    @EruptField(
            views = @View(title = "品牌名称"),
            edit = @Edit(title = "品牌名称", notNull = true, search = @Search(vague = true))
    )
    private String name;

    @EruptField(
            views = @View(title = "品牌首字母"),
            edit = @Edit(title = "品牌首字母", notNull = true)
    )
    private String firstLetter;

    @EruptField(
            views = @View(title = "品牌LOGO"),
            edit = @Edit(title = "品牌LOGO", type = EditType.ATTACHMENT,
                    attachmentType = @AttachmentType(type = AttachmentType.Type.IMAGE))
    )
    private String logo;

    @EruptField(
            views = @View(title = "品牌专区大图"),
            edit = @Edit(title = "品牌专区大图", type = EditType.ATTACHMENT,
                    attachmentType = @AttachmentType(type = AttachmentType.Type.IMAGE))
    )
    private String bigPic;

    @EruptField(
            views = @View(title = "品牌故事"),
            edit = @Edit(title = "品牌故事", type = EditType.TEXTAREA)
    )
    private String brandStory;

    @Transient //由于该字段不需要持久化，所以使用该注解修饰
    @EruptField(
            edit = @Edit(title = "额外信息", type = EditType.DIVIDE)
    )
    private String divide;

    @EruptField(
            views = @View(title = "排序"),
            edit = @Edit(title = "排序")
    )
    private Integer sort;

    @EruptField(
            views = @View(title = "是否显示"),
            edit = @Edit(title = "是否显示")
    )
    private Boolean showStatus;

    @EruptField(
            views = @View(title = "品牌制造商"),
            edit = @Edit(title = "品牌制造商")
    )
    private Boolean factoryStatus;

    private Integer productCount;

    private Integer productCommentCount;

}
