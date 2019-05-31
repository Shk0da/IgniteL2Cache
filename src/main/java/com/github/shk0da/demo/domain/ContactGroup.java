package com.github.shk0da.demo.domain;

import com.github.shk0da.demo.model.contacts.ContactGroupModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Data
@Entity
@Builder
@Cacheable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contact_group")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ContactGroup implements Serializable {

    private static final long serialVersionUID = -5037390197203452046L;

    @Id
    @GeneratedValue
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    private String name;
    private String description;

    public static ContactGroup of(ContactGroupModel contactGroupModel) {
        return ContactGroup.builder()
                .name(contactGroupModel.getName())
                .description(contactGroupModel.getDescription())
                .build();
    }
}
