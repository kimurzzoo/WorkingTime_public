package com.workingtime.auth.account.entity

import jakarta.persistence.*

@Entity
@Table(name="user")
class User(nickname : String, emailAddress : String, m_password : String, role : String) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id : Long? = null

    @Column(name = "nickname", columnDefinition = "varchar(200)", nullable = false)
    var nickname : String = nickname

    @Column(name = "email_address", columnDefinition = "varchar(200)", nullable = false, unique = true)
    var emailAddress : String = emailAddress

    @Column(name = "password", columnDefinition = "varchar(200)", nullable = false)
    var m_password : String = m_password

    @Column(name = "role", columnDefinition = "varchar(200)", nullable = false)
    var role : String = role

    @Column(name = "enabled", nullable = false)
    var enabled : Boolean = true

    @Column(name = "verified", nullable = false)
    var verified : Boolean = false
}