package com.javalab.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sound.midi.Soundbank;

public class Board { 
	public static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
	public static final String DB_URL = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
	
	public static Connection con = null;
	public static PreparedStatement pstmt = null;
	public static ResultSet rs = null;
	
	public static String oracleId = "board";
	public static String oraclePwd = "5678";
	
	
	
	public static void main(String[] args) {
		
		//1. 디비 접속 메소드호출
		connectDB();
		
//		//2.게시물 등록 조회
//		getBoardList();
//		
//		//3. 새글등록
//		// 새글 등록이 완료되었으면 주석처리 한후에 답글등록으로 이동
//		insertNewBoard();
//		
//		//4. 답글등록
//		//어떤 게시물에 답글을 달지 부모 게시글의 정보를 전달해야 함.
//		int replyGroup = 53;  //부모글의 그룹번호
//		int replyOrder = 0;    //부모글의 그룹내 순서
//		int replyIndent = 0;		// 부모글의 들여쓰기
//		
//		insertReply(replyGroup, replyOrder,replyIndent);
//		
//		//5. 게시물 목록 조회(반드시 1번~ 5번까지)
//		int startNo = 1;
//		int length = 5;
//		getBoardListTopN(startNo,length);
//		
//		//6. 중간에 특정 부분 조회(5번 ~ 9번까지)
//		startNo = 5;
//		length = 9;
//		getBoardListPart(startNo, length);
//		
//		//7. 게시물 조회수 증가
//		int bno = 53; //조회수를 증가시킬 게시물 번호
//		updateCount(bno);
//		
//		//8. 수정
//		// 5번 게시물의 제목을 "다섯번째 글"로 수정하세요.
//		int bno = 47;
//		String newTitle = "다섯번째 글";
//		updateTitle(bno,newTitle);
		
		//9 user01님이 작성한 게시물을 모두 삭제하세요.
		int bno = 51; //삭제할 게시물 번호
		deleteBoard(bno);
	
		//자원반납
		closeResource();		
		
	} //end main
	
	//9 user01님이 작성한 게시물을 모두 삭제하세요.
	private static void deleteBoard(int bno) {
		String sql = "";
		try {
			sql = "delete from tbl_board";
			sql += " where bno = ?";
			
			
			pstmt = con.prepareStatement(sql);
			
			
			pstmt.setInt(1, bno);
			
			int resultRows = pstmt.executeUpdate();
			if (resultRows > 0) {
				System.out.println(" 게시물 삭제 성공");
			}else {
				System.out.println(" 게시물 삭제 실패");
			}
			System.out.println();
		} catch (SQLException e) {
			System.out.println("SQL ERR :" +e.getMessage());
		}finally {
			closeResource(pstmt, rs);
		}
	}

	//8. 수정
	// 5번 게시물의 제목을 "다섯번째 글"로 수정하세요.
	private static void updateTitle(int bno, String newTitle) {
		String sql = "";
		try {
			sql = "update tbl_board";
			sql += " set title = ? ";
			sql += " where bno = ? ";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, newTitle);
			pstmt.setInt(2, bno);
			
			int resultRows = pstmt.executeUpdate();
			if (resultRows > 0) {
				System.out.println("타이틀 수정 성공");
			}else {
				System.out.println("타이틀 수정 실패");
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR ! :" +e.getMessage() );
		}finally {
			closeResource(pstmt, rs);
		}
		
	}


	//7. 게시물 조회수 증가
	private static void updateCount(int bno) {
		String sql = "";
		try {
			sql = "update tbl_board";
			sql += " set count = count +1";
			sql += " where bno = ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, bno);
			
			int resultRows = pstmt.executeUpdate();
			if (resultRows > 0) {
				System.out.println("조회수 증가 성공");
			}else {
				System.out.println("조회수 증가 실패");
			}

		} catch (SQLException e) {
			System.out.println("SQL ERR ! : " + e.getMessage());
		}finally {
			closeResource(pstmt, rs);
		}
		
	}



	//6. 중간에 특정 부분 조회(5번 ~ 9번까지)
	private static void getBoardListPart(int startNo, int length) {
		String sql = "";
		try {
			sql = "select b.*\r\n" + 
					"from(\r\n" + 
					"    select rownum rnum, a.*\r\n" + 
					"    from(\r\n" + 
					"        select b.*\r\n" + 
					"        from tbl_board b  \r\n" + 
					"         order by b.created_date desc\r\n" + 
					"     ) a \r\n" + 
					") b \r\n" + 
					"where rnum between ? and ?";
			System.out.println("sql :" + sql);
			pstmt = con.prepareStatement(sql);
			System.out.println("pstmt 객체 생성 성공 ");
			
			pstmt.setInt(1, startNo);
			pstmt.setInt(2, length);
			
			rs = pstmt.executeQuery();
			
			System.out.println( startNo + " 번 부터" + length + " 개까지 조회 결과");
			
			while (rs.next()) {
				String strInd = "";
				int indent = rs.getInt("reply_indent");
				//이부분은 답글쓸때 들여쓰기 위한거
				if (indent > 0) {
					for (int i = 0; i < indent; i++) {
						strInd += " ";
					}
				}
				System.out.println(strInd+rs.getInt("bno") + " " 
						+ rs.getInt("reply_group")+ " " 
						+ rs.getInt("reply_order") + " " 
						+rs.getInt("reply_indent") + " " 
						+ rs.getString("title") + " " 
						+ rs.getString("member_id") + " " 
						+ rs.getInt("count") + " " 
						+ rs.getDate("created_date"));
			}
			
		} catch (SQLException e) {
			System.out.println("SQL ERR : " + e.getMessage());
		}finally {
			closeResource(pstmt, rs);
			
		}
		
	}




	//5. 게시물 목록 조회(반드시 1번~ 5번까지)
	private static void getBoardListTopN(int startNo, int length) {
		String sql = "";
		try {
			sql ="select a.*\r\n" + 
					"from(\r\n" + 
					"    select rownum rnum, b.*\r\n" + 
					"    from(\r\n" + 
					"        select *\r\n" + 
					"        from tbl_board \r\n" + 
					"        order by reply_group desc\r\n" + 
					"    ) b\r\n" + 
					")a    \r\n" + 
					"where rnum between ? and ?";
			
			System.out.println("sql :" + sql);
			
			pstmt = con.prepareStatement(sql);
			System.out.println("pstmt 객체 생성 성공");
			
			pstmt.setInt(1, startNo);
			pstmt.setInt(2, length);
			
			rs = pstmt.executeQuery();
			
			System.out.println( startNo + " 번 부터" + length + " 개까지 조회 결과");
			
			while (rs.next()) {
				String strInd = "";
				int indent = rs.getInt("reply_indent");
				//이부분은 답글쓸때 들여쓰기 위한거
				if (indent > 0) {
					for (int i = 0; i < indent; i++) {
						strInd += " ";
					}
				}
				System.out.println(strInd + rs.getInt("bno") + " " 
						+ rs.getInt("reply_group")+ " " 
						+ rs.getInt("reply_order") + " " 
						+rs.getInt("reply_indent") + " " 
						+ rs.getString("title") + " " 
						+ rs.getString("member_id") + " " 
						+ rs.getInt("count") + " " 
						+ rs.getDate("created_date"));
				
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR : " +e.getMessage());
		}finally {
			closeResource(pstmt, rs);
		}
		
	}

	//4. 답글등록
	private static void insertReply(int replyGroup, int replyOrder, int replyIndent) {
		String sql = "";
		
		try {
			String title = "[답글2] 프로그램을 통해 등록한 새글";
			String content = "[답글2] 프로그램을 통해 등록한 새글 내용";
			String memberId = "user02";
			
			/*
			 * [1. 현재 부모글에 달린 답글들의 그룹내 순서 증가(+1)]
			 */
			sql = "update tbl_board";
			sql += " set reply_order = reply_order + 1";
			sql += " where reply_group = ?";
			sql += " and reply_order > ? ";
			
			pstmt =con.prepareStatement(sql);
			pstmt.setInt(1, replyGroup);
			pstmt.setInt(2, replyIndent);
			
			int resultRow = pstmt.executeUpdate();
			if (resultRow > 0) {
				System.out.println("기존 답글의 order 컬럼 +1 변경 성공");
			}else {
				System.out.println("기존 답글의 order 컬럽 +1 변경 실패");
			}
			
			sql = "";
			sql = "insert into tbl_board(bno, title, content, member_id, count,";
			sql += " created_date, reply_group, reply_order, reply_indent)";
			sql += " values(seq_bno.nextval, ? , ? , ?, 0, sysdate, ?, ?, ?)";
			
			pstmt =con.prepareStatement(sql);
			
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			pstmt.setString(3, memberId);
			pstmt.setInt(4, replyGroup);
			pstmt.setInt(5, replyOrder + 1);
			pstmt.setInt(6, replyIndent + 1);
			
			int resultRows = pstmt.executeUpdate();
			if (resultRow > 0) {
				System.out.println("답글 저장 성공");
			}else {
				System.out.println("답글 저장 실패");
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR ! : " + e.getMessage());
		}finally {
			closeResource(pstmt, rs);
		}
		
	}


	//3. 새글등록
	private static void insertNewBoard() {
		String sql = "";
		//저장할 게시물 세팅
		try {
			String title = "프로그램 을 통해 등록한 새글";
			String content = "프로그램을 통해 등록한 새글 내용";
			String memberId = "user03";
			
			int replyOrder = 0;  //order는 부모 order +1(부모 다음으로 위치하도록)
			int replyIndent = 0; //indent는 부모 indent +1(부모보다 한 칸 들여쓰기)
			
			//PreparedStatement 객체에 사용할 SQL문 생성
			sql = "insert into tbl_board(bno, title, content, member_id, count,";
			sql += " created_date, reply_group, reply_order, reply_indent)";
			sql += " values(seq_bno.nextval, ? , ? , ?, 0, sysdate, seq_bno.currval, ?, ?)";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			pstmt.setString(3, memberId);
			pstmt.setInt(4, replyOrder);
			pstmt.setInt(5, replyIndent);
			
			int resultRows = pstmt.executeUpdate();
	         
	         if (resultRows > 0) {
				System.out.println("저장 성공");
			}else {
				System.out.println("저장 실패");
			}
			
			
		} catch (SQLException e) {
			System.out.println("SQL ERR !:" + e.getMessage());
		}finally {
			closeResource(pstmt, rs);
		}
		
	}


	 //2.게시물 등록 조회
	private static void getBoardList() {
		String sql = "";
		try {
			sql = "select * from tbl_board";
			
			pstmt = con.prepareStatement(sql);
			System.out.println("pstmt 객체 생성 성공");
			
			rs = pstmt.executeQuery();
			
			System.out.println(" 2번 게시물 등록 조회");
			System.out.println();
			
			System.out.println("보드게시판 정보조회");
			while (rs.next()) {
				System.out.println(rs.getInt("bno") + " " + rs.getString("title")+ " " 
								+ rs.getString("content") + " " +rs.getString("member_id") + " " 
								+ rs.getInt("count") + " " + rs.getDate("created_date"));
			}
			
		} catch (SQLException e) {
			System.out.println(" SQL ERR ! : " + e.getMessage());
		}finally {
			closeResource(pstmt, rs);
		}
		
	}


	//1. 디비 접속 메소드호출
	private static void connectDB() {
		try {
			Class.forName(DRIVER_NAME);
			System.out.println("1. 드라이버 로드 성공!");
			
			con = DriverManager.getConnection(DB_URL,oracleId,oraclePwd);
			System.out.println("2. 커넥션 객체 생성 성공");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 ERR ! : " + e.getMessage());
		}catch (SQLException e) {
			System.out.println("SQL ERR ! : " +e.getMessage());
		}
		
	}
	private static void closeResource(PreparedStatement pstmt,ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			System.out.println("Result, PreparedStatement 자원 반납 완료");
		} catch (SQLException e) {
			System.out.println("자원 해제 ERR ! : " +e.getMessage());
		}
		
	}
	private static void closeResource() {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			System.out.println("자원 해제 ERR ! : " +e.getMessage());
		}
		
	}

	
	
	
	
	
	
} // end class
