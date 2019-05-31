package com.github.shk0da.demo.domain;

import com.github.shk0da.demo.aop.audit.EntityAuditListener;
import com.github.shk0da.demo.model.contacts.ContactModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

@Data
@Entity
@Builder
@Cacheable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contact", indexes = {
        @Index(columnList = "contact_group_id", name = "contact_group_id_idx"),
        @Index(columnList = "contact_group_id, first_name, last_name", name = "contact_group_id_first_name_last_name_idx"),
        @Index(columnList = "id, contact_group_id, first_name, last_name", name = "id_contact_group_id_first_name_last_name_idx"),
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@EntityListeners(value = {EntityAuditListener.class})
public class Contact implements AuditEntity, Serializable {

    private static final long serialVersionUID = 7827081745195113484L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT), columnDefinition = "contact_group_id")
    private ContactGroup contactGroup;

    public static Contact of(ContactModel contactModel, ContactGroup group) {
        Contact.ContactBuilder builder = Contact.builder()
                .contactGroup(group)
                .firstName(contactModel.getFirstName())
                .lastName(contactModel.getLastName())
                .birthday(contactModel.getBirthday());
        return builder.build();
    }

    public void path(Contact contactPatch) {
        for (Field field : Contact.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            try {
                Object value = field.get(contactPatch);
                if (value != null) {
                    field.set(this, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(accessible);
        }
    }
}
