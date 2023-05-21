package com.workingtime.chat.check.entity

import com.workingtime.chat.account.entity.User
import jakarta.persistence.*

@Entity
@Table(name = "company")
class Company (
    @Column(name = "name", nullable = false, unique = true, columnDefinition = "VARCHAR(30)")
    var name : String
    )
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id : Long? = null

    @Column(name = "enabled", nullable = false)
    var enabled : Boolean = true

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var users : MutableList<User> = mutableListOf()
}