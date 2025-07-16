package com.example.mapper;

import com.example.entity.Student;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


import java.util.List;
public interface StudentMapper {
    void insert(Student student);

    @Select("select * from student where username = #{username}")
    Student selectByUsername(String username);

    @Select("select student.*, college.name as collegeName from student left join college on student.college_id = college.id")
    List<Student> selectAll();

    @Select("select student.*, college.name as collegeName from student " +
            "left join college on student.college_id = college.id " +
            "where student.name like concat('%', #{name}, '%')")
    List<Student> selectByName(String name);

    void updateById(Student student);

    @Delete("delete from student where id = #{id}")
    void deleteById(Integer id);

    @Select("select student.*, college.name as collegeName from student " +
            "left join college on student.college_id = college.id " +
            "where student.id = #{id}")
    Student selectById(Integer id);

    // 根据用户名查询学生
    @Select("SELECT * FROM student WHERE username = #{username}")
    Student findByUsername(String username);

    // 更新学生信息（含密码，字段按需调整，这里用动态 SQL 或明确字段更严谨，示例简化 ）
    @Update("UPDATE student SET password = #{password}, " +
            "name = #{name}, sex = #{sex} " + // 其他需更新的字段，按实际需求
            "WHERE id = #{id}")
    int update(Student student);
}
