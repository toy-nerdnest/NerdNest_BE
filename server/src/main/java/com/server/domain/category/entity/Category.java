package com.server.domain.category.entity;


import com.server.domain.audit.Auditable;
import com.server.domain.blog.entity.Blog;
import com.server.domain.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long categoryId;

	@Column(name = "category_name")
	private String categoryName;

	@OneToMany(mappedBy = "category", orphanRemoval = true)
	private List<Blog> blogList;

	@ManyToOne
	@JoinColumn(name = "memberId")
	private Member member;

	@Builder
	public Category(Long categoryId, String categoryName, Member member) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.member = member;
	}

}
