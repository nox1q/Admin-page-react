package kz.iitu.csse.group34.entities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_orders")
public class Orders extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Books book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "status_id")
    private int status_id;

}