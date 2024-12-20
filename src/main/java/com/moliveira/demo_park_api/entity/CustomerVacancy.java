package com.moliveira.demo_park_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer_vacancy")
@EntityListeners(AuditingEntityListener.class)
public class CustomerVacancy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "receipt_number", nullable = false, unique = true, length = 15)
    private String receipt;
    @Column(name = "license_plate", nullable = false, length = 8)
    private String licensePlate;
    @Column(name = "brand", nullable = false, length = 45)
    private String brand;
    @Column(name = "model", nullable = false, length = 45)
    private String model;
    @Column(name = "color", nullable = false, length = 45)
    private String color;
    @Column(name = "entry_date", nullable = false)
    private LocalDateTime entryDate;
    @Column(name = "departure_date")
    private LocalDateTime departureDate;
    @Column(name = "amount", columnDefinition = "decimal(7,2)")
    private BigDecimal amount;
    @Column(name = "discount", columnDefinition = "decimal(7,2)")
    private BigDecimal discount;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;
    @ManyToOne
    @JoinColumn(name = "id_space", nullable = false)
    private ParkingSpace space;

    @CreatedDate
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CustomerVacancy that = (CustomerVacancy) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
