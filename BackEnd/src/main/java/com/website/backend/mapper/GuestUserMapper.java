package com.website.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.website.backend.entity.GuestUser;
import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;

@Mapper
public interface GuestUserMapper extends BaseMapper<GuestUser> {
    
    Optional<GuestUser> findByUsername(String username);
    
    int deleteExpiredGuests();
}