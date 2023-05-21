package com.workingtime.check.account.entity

import com.workingtime.check.check.entity.Check
import com.workingtime.check.check.entity.Company
import jakarta.persistence.*

@Entity
@Table(name="user")
class User (
    @Column(name = "nickname", columnDefinition = "varchar(200)", nullable = false)
    var nickname : String,

    @Column(name = "email_address", columnDefinition = "varchar(200)", nullable = false, unique = true)
    var emailAddress : String,

    @Column(name = "password", columnDefinition = "varchar(200)", nullable = false)
    var m_password : String,

    @Column(name = "role", columnDefinition = "varchar(200)", nullable = false)
    var role : String
    ) : BaseEntity()
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id : Long? = null

    @Column(name = "enabled", nullable = false)
    var enabled : Boolean = true

    @Column(name = "verified", nullable = false)
    var verified : Boolean = false

    @ManyToOne
    @JoinColumn(name = "company_id")
    var company : Company? = null

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL],)
    @OrderBy("id desc")
    var checks : MutableList<Check> = mutableListOf()
}