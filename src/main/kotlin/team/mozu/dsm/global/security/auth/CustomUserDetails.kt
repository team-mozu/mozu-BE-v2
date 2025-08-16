package com.example.kotlin_practice.global.security.auth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import team.mozu.dsm.domain.organ.model.Organ

class CustomUserDetails(val organ: Organ): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

    override fun getPassword(): String? = null

    override fun getUsername(): String = organ.organCode

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
