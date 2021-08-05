package com.mrru;

import com.mrru.annotation.Service;

/**
 * @className: ByeServiceImpl
 * @author: 茹某
 * @date: 2021/8/5 17:03
 **/
@Service
public class ByeServiceImpl implements ByeService
{

    @Override
    public String bye(String name)
    {
        return "bye, " + name;
    }
}
