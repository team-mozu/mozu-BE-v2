package team.mozu.dsm.adapter.out.lesson.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.EmbeddedId
import jakarta.persistence.MapsId
import jakarta.persistence.ManyToOne
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.Column
import team.mozu.dsm.adapter.out.item.entity.ItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.id.LessonItemId

@Entity
@Table(name = "tbl_lesson_item")
class LessonItemJpaEntity(

    @EmbeddedId
    var lessonItemId: LessonItemId,

    @MapsId("lessonId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    var lesson: LessonJpaEntity,

    @MapsId("itemId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    var item: ItemJpaEntity,

    @Column(nullable = false)
    var round1Price: Long,

    @Column(nullable = false)
    var round2Price: Long,

    @Column(nullable = false)
    var round3Price: Long,

    var round4Price: Long?,

    var round5Price: Long?,

    @Column(nullable = false)
    var endPrice: Long
)
