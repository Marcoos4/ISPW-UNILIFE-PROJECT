-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:8889
-- Creato il: Feb 14, 2026 alle 18:04
-- Versione del server: 8.0.44
-- Versione PHP: 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `unilife_db`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `application`
--

CREATE TABLE `application` (
  `course_title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `university_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `creation_date` datetime NOT NULL,
  `student_username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `submission_date` datetime NOT NULL,
  `status` varchar(25) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `application`
--

INSERT INTO `application` (`course_title`, `university_name`, `creation_date`, `student_username`, `submission_date`, `status`) VALUES
('Computer Science', 'Tor Vergata', '2026-02-08 20:40:45', 'marco', '2026-02-08 20:40:44', 'Submitted');

-- --------------------------------------------------------

--
-- Struttura della tabella `application_item`
--

CREATE TABLE `application_item` (
  `course_title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `university_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `creation_date` datetime NOT NULL,
  `student_username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `requirement_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `type` varchar(25) COLLATE utf8mb4_general_ci NOT NULL,
  `text` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `document` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `application_item`
--

INSERT INTO `application_item` (`course_title`, `university_name`, `creation_date`, `student_username`, `requirement_name`, `type`, `text`, `document`) VALUES
('Computer Science', 'Tor Vergata', '2026-02-08 20:40:45', 'marco', 'CS-ML', 'Text', 'Motivation letter', NULL);

-- --------------------------------------------------------

--
-- Struttura della tabella `application_notification`
--

CREATE TABLE `application_notification` (
  `username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `timestamp` datetime NOT NULL,
  `status` varchar(25) COLLATE utf8mb4_general_ci NOT NULL,
  `message` varchar(250) COLLATE utf8mb4_general_ci NOT NULL,
  `course_title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `university_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `student_username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `creation_date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `application_notification`
--

INSERT INTO `application_notification` (`username`, `timestamp`, `status`, `message`, `course_title`, `university_name`, `student_username`, `creation_date`) VALUES
('ale', '2026-02-08 20:40:59', 'Pending', 'Your application has been Accepted', 'Computer Science', 'Tor Vergata', 'marco', '2026-02-08 20:40:45'),
('marco', '2026-02-08 20:40:44', 'Completed', 'New application for Computer Science', 'Computer Science', 'Tor Vergata', 'marco', '2026-02-08 20:40:45');

-- --------------------------------------------------------

--
-- Struttura della tabella `course`
--

CREATE TABLE `course` (
  `title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(200) COLLATE utf8mb4_general_ci NOT NULL,
  `university_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `duration` int NOT NULL,
  `fees` double NOT NULL,
  `course_type` varchar(25) COLLATE utf8mb4_general_ci NOT NULL,
  `language` varchar(50) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `course`
--

INSERT INTO `course` (`title`, `description`, `university_name`, `duration`, `fees`, `course_type`, `language`) VALUES
('Computer Science', 'Computer Science', 'Tor Vergata', 24, 2100, 'Postgraduate', 'Italian'),
('Ingegneria Informatica', 'Ingegneria informatica', 'Tor Vergata', 36, 2000, 'Undergraduate', 'Italian'),
('Matematica', 'phd', 'Tor Vergata', 36, 1000, 'Phd', 'Italian');

-- --------------------------------------------------------

--
-- Struttura della tabella `course_notification`
--

CREATE TABLE `course_notification` (
  `username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `timestamp` datetime NOT NULL,
  `status` varchar(25) COLLATE utf8mb4_general_ci NOT NULL,
  `message` varchar(250) COLLATE utf8mb4_general_ci NOT NULL,
  `course_title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `university_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `course_tags`
--

CREATE TABLE `course_tags` (
  `course_title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `university_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `tag` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `course_tags`
--

INSERT INTO `course_tags` (`course_title`, `university_name`, `tag`) VALUES
('Computer Science', 'Tor Vergata', 'Math'),
('Ingegneria Informatica', 'Tor Vergata', 'Math'),
('Matematica', 'Tor Vergata', 'Math');

-- --------------------------------------------------------

--
-- Struttura della tabella `document`
--

CREATE TABLE `document` (
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `type` varchar(25) COLLATE utf8mb4_general_ci NOT NULL,
  `size` double NOT NULL,
  `content` blob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `document_requirement`
--

CREATE TABLE `document_requirement` (
  `course_title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `university_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `max_size` double NOT NULL,
  `allowed_extension` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `is_certificate` tinyint(1) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `label` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `interested_student`
--

CREATE TABLE `interested_student` (
  `username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `course_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `university_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `lesson`
--

CREATE TABLE `lesson` (
  `subject` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `price` float NOT NULL,
  `start` datetime NOT NULL,
  `end` datetime NOT NULL,
  `duration` int NOT NULL,
  `tutor_username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `status` varchar(25) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `lesson`
--

INSERT INTO `lesson` (`subject`, `price`, `start`, `end`, `duration`, `tutor_username`, `status`) VALUES
('Italiano', 10, '2026-02-09 07:30:00', '2026-02-09 10:30:00', 3, 'lore', 'Accepted'),
('Ingegneria infor', 15, '2026-02-09 09:00:00', '2026-02-09 14:00:00', 5, 'lore', 'Accepted'),
('Math', 24, '2026-02-10 09:00:00', '2026-02-10 11:00:00', 2, 'lore', 'Accepted');

-- --------------------------------------------------------

--
-- Struttura della tabella `lesson_notification`
--

CREATE TABLE `lesson_notification` (
  `username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `timestamp` datetime NOT NULL,
  `status` varchar(25) COLLATE utf8mb4_general_ci NOT NULL,
  `message` varchar(250) COLLATE utf8mb4_general_ci NOT NULL,
  `start` datetime NOT NULL,
  `tutor_username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `lesson_notification`
--

INSERT INTO `lesson_notification` (`username`, `timestamp`, `status`, `message`, `start`, `tutor_username`) VALUES
('ale', '2026-02-08 20:36:52', 'Pending', 'Your Lesson has been Accepted', '2026-02-10 09:00:00', 'lore'),
('ale', '2026-02-08 20:36:56', 'Pending', 'Your Lesson has been Accepted', '2026-02-09 07:30:00', 'lore'),
('ale', '2026-02-08 20:40:02', 'Pending', 'Your Lesson has been Accepted', '2026-02-09 09:00:00', 'lore'),
('lore', '2026-02-08 20:34:54', 'Completed', 'New lesson available: Italiano', '2026-02-09 07:30:00', 'lore'),
('lore', '2026-02-08 20:36:40', 'Completed', 'New lesson available: Math', '2026-02-10 09:00:00', 'lore'),
('lore', '2026-02-08 20:39:47', 'Completed', 'New lesson available: Ingegneria infor', '2026-02-09 09:00:00', 'lore');

-- --------------------------------------------------------

--
-- Struttura della tabella `payment`
--

CREATE TABLE `payment` (
  `amount` float NOT NULL,
  `status` varchar(25) COLLATE utf8mb4_general_ci NOT NULL,
  `payment_stripe` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `payment`
--

INSERT INTO `payment` (`amount`, `status`, `payment_stripe`) VALUES
(75, 'Paid', 'pi_3SyenUEC114jaobB04HsxELx'),
(48, 'Paid', 'pi_3SzjUZEC114jaobB10xY8BxS');

-- --------------------------------------------------------

--
-- Struttura della tabella `reservation`
--

CREATE TABLE `reservation` (
  `student_username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `tutor_username_lesson` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `start_date_time` datetime NOT NULL,
  `status` varchar(25) COLLATE utf8mb4_general_ci NOT NULL,
  `payment_stripe` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `reservation`
--

INSERT INTO `reservation` (`student_username`, `tutor_username_lesson`, `start_date_time`, `status`, `payment_stripe`) VALUES
('marco', 'lore', '2026-02-09 09:00:00', 'Payed', 'pi_3SyenUEC114jaobB04HsxELx'),
('marco', 'lore', '2026-02-10 09:00:00', 'Payed', 'pi_3SzjUZEC114jaobB10xY8BxS');

-- --------------------------------------------------------

--
-- Struttura della tabella `reservation_notification`
--

CREATE TABLE `reservation_notification` (
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `timestamp` datetime NOT NULL,
  `status` varchar(25) COLLATE utf8mb4_general_ci NOT NULL,
  `message` varchar(250) COLLATE utf8mb4_general_ci NOT NULL,
  `student_username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `tutor_username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `start` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `reservation_notification`
--

INSERT INTO `reservation_notification` (`username`, `timestamp`, `status`, `message`, `student_username`, `tutor_username`, `start`) VALUES
('lore', '2026-02-08 20:41:12', 'Completed', 'Your reservation has been Confirmed', 'marco', 'lore', '2026-02-09 09:00:00'),
('lore', '2026-02-11 19:54:11', 'Completed', 'Your reservation has been Confirmed', 'marco', 'lore', '2026-02-10 09:00:00'),
('marco', '2026-02-08 20:40:26', 'Completed', 'New reservation request from marco', 'marco', 'lore', '2026-02-09 09:00:00'),
('marco', '2026-02-11 19:54:00', 'Completed', 'New reservation request from marco', 'marco', 'lore', '2026-02-10 09:00:00');

-- --------------------------------------------------------

--
-- Struttura della tabella `student`
--

CREATE TABLE `student` (
  `username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `budget` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `student`
--

INSERT INTO `student` (`username`, `budget`) VALUES
('marco', 0);

-- --------------------------------------------------------

--
-- Struttura della tabella `student_interest`
--

CREATE TABLE `student_interest` (
  `student_username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `tag` varchar(25) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struttura della tabella `text_requirement`
--

CREATE TABLE `text_requirement` (
  `course_title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `university_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `min_char` int NOT NULL,
  `max_char` int NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `label` varchar(25) COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `text_requirement`
--

INSERT INTO `text_requirement` (`course_title`, `university_name`, `min_char`, `max_char`, `name`, `label`, `description`) VALUES
('Computer Science', 'Tor Vergata', 10, 100, 'CS-ML', 'CS-ML', 'MOTIVATION LETTER'),
('Ingegneria Informatica', 'Tor Vergata', 10, 100, 'IF-ML', 'IF-ML', 'MOTIVATION LETTER'),
('Matematica', 'Tor Vergata', 10, 100, 'ML', 'ML', 'MOTIVATION LETTER');

-- --------------------------------------------------------

--
-- Struttura della tabella `tutor`
--

CREATE TABLE `tutor` (
  `username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `rating` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `tutor`
--

INSERT INTO `tutor` (`username`, `rating`) VALUES
('lore', 0);

-- --------------------------------------------------------

--
-- Struttura della tabella `university`
--

CREATE TABLE `university` (
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `location` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `ranking` int NOT NULL,
  `living_cost` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `university`
--

INSERT INTO `university` (`name`, `location`, `ranking`, `living_cost`) VALUES
('Oxford', 'England', 1, 30000),
('Sapienza', 'Italy', 1000, 2000),
('Tor Vergata', 'Italy', 10000, 15000),
('TUMunich', 'Germany', 17, 7000);

-- --------------------------------------------------------

--
-- Struttura della tabella `university_employee`
--

CREATE TABLE `university_employee` (
  `username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `university_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `university_employee`
--

INSERT INTO `university_employee` (`username`, `university_name`) VALUES
('ale', 'Tor Vergata');

-- --------------------------------------------------------

--
-- Struttura della tabella `user`
--

CREATE TABLE `user` (
  `username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `surname` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `role` varchar(25) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `user`
--

INSERT INTO `user` (`username`, `name`, `surname`, `password`, `role`) VALUES
('ale', 'Alessandro', 'Zirilli', 'a', 'University_employee'),
('lore', 'Lorenzo', 'Cellitti', 'a', 'Tutor'),
('marco', 'Marco', 'Zirilli', 'a', 'Student');

-- --------------------------------------------------------

--
-- Struttura della tabella `user_notification`
--

CREATE TABLE `user_notification` (
  `username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `sender_username` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `timestamp` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `user_notification`
--

INSERT INTO `user_notification` (`username`, `sender_username`, `timestamp`) VALUES
('lore', 'ale', '2026-02-08 20:36:52'),
('lore', 'ale', '2026-02-08 20:36:56'),
('lore', 'ale', '2026-02-08 20:40:02'),
('marco', 'ale', '2026-02-08 20:40:59'),
('ale', 'lore', '2026-02-08 20:34:54'),
('ale', 'lore', '2026-02-08 20:36:40'),
('ale', 'lore', '2026-02-08 20:39:47'),
('marco', 'lore', '2026-02-08 20:41:12'),
('marco', 'lore', '2026-02-11 19:54:11'),
('lore', 'marco', '2026-02-08 20:40:26'),
('ale', 'marco', '2026-02-08 20:40:44'),
('lore', 'marco', '2026-02-11 19:54:00');

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `application`
--
ALTER TABLE `application`
  ADD PRIMARY KEY (`course_title`,`university_name`,`creation_date`,`student_username`),
  ADD KEY `fk_student_app` (`student_username`),
  ADD KEY `idx_fk_application` (`course_title`,`university_name`,`student_username`,`creation_date`);

--
-- Indici per le tabelle `application_item`
--
ALTER TABLE `application_item`
  ADD PRIMARY KEY (`course_title`,`university_name`,`creation_date`,`student_username`,`requirement_name`),
  ADD KEY `idx_fk_application_item` (`course_title`,`university_name`,`student_username`,`creation_date`);

--
-- Indici per le tabelle `application_notification`
--
ALTER TABLE `application_notification`
  ADD PRIMARY KEY (`username`,`timestamp`),
  ADD KEY `fk_appplication_notification` (`course_title`,`university_name`,`student_username`,`creation_date`);

--
-- Indici per le tabelle `course`
--
ALTER TABLE `course`
  ADD PRIMARY KEY (`university_name`,`title`),
  ADD KEY `idx_course_univeristy` (`title`,`university_name`);

--
-- Indici per le tabelle `course_notification`
--
ALTER TABLE `course_notification`
  ADD PRIMARY KEY (`username`,`timestamp`),
  ADD KEY `fk_course_notification` (`course_title`,`university_name`);

--
-- Indici per le tabelle `course_tags`
--
ALTER TABLE `course_tags`
  ADD PRIMARY KEY (`course_title`,`university_name`,`tag`);

--
-- Indici per le tabelle `document`
--
ALTER TABLE `document`
  ADD PRIMARY KEY (`name`);

--
-- Indici per le tabelle `document_requirement`
--
ALTER TABLE `document_requirement`
  ADD PRIMARY KEY (`course_title`,`university_name`,`name`);

--
-- Indici per le tabelle `interested_student`
--
ALTER TABLE `interested_student`
  ADD PRIMARY KEY (`username`,`course_name`,`university_name`),
  ADD KEY `fk_inter_cour` (`course_name`,`university_name`);

--
-- Indici per le tabelle `lesson`
--
ALTER TABLE `lesson`
  ADD PRIMARY KEY (`tutor_username`,`start`),
  ADD KEY `idx_lesson_start_tutor` (`start`,`tutor_username`);

--
-- Indici per le tabelle `lesson_notification`
--
ALTER TABLE `lesson_notification`
  ADD PRIMARY KEY (`username`,`timestamp`),
  ADD KEY `fk_lesson_notification` (`start`,`tutor_username`);

--
-- Indici per le tabelle `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`payment_stripe`);

--
-- Indici per le tabelle `reservation`
--
ALTER TABLE `reservation`
  ADD PRIMARY KEY (`student_username`,`tutor_username_lesson`,`start_date_time`),
  ADD KEY `fk_lesson` (`tutor_username_lesson`,`start_date_time`);

--
-- Indici per le tabelle `reservation_notification`
--
ALTER TABLE `reservation_notification`
  ADD PRIMARY KEY (`username`,`timestamp`),
  ADD KEY `fk_reservation_notification` (`student_username`,`tutor_username`,`start`);

--
-- Indici per le tabelle `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`username`);

--
-- Indici per le tabelle `student_interest`
--
ALTER TABLE `student_interest`
  ADD PRIMARY KEY (`student_username`,`tag`);

--
-- Indici per le tabelle `text_requirement`
--
ALTER TABLE `text_requirement`
  ADD PRIMARY KEY (`course_title`,`university_name`,`name`);

--
-- Indici per le tabelle `tutor`
--
ALTER TABLE `tutor`
  ADD PRIMARY KEY (`username`);

--
-- Indici per le tabelle `university`
--
ALTER TABLE `university`
  ADD PRIMARY KEY (`name`);

--
-- Indici per le tabelle `university_employee`
--
ALTER TABLE `university_employee`
  ADD PRIMARY KEY (`username`),
  ADD KEY `fk_uni_employee` (`university_name`);

--
-- Indici per le tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`username`);

--
-- Indici per le tabelle `user_notification`
--
ALTER TABLE `user_notification`
  ADD PRIMARY KEY (`username`,`sender_username`,`timestamp`),
  ADD KEY `fk_lesson_user_notification` (`sender_username`,`timestamp`),
  ADD KEY `fk_application_user_notification` (`username`,`timestamp`);

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `application`
--
ALTER TABLE `application`
  ADD CONSTRAINT `fk_course_app` FOREIGN KEY (`course_title`,`university_name`) REFERENCES `course` (`title`, `university_name`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_student_app` FOREIGN KEY (`student_username`) REFERENCES `student` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `application_item`
--
ALTER TABLE `application_item`
  ADD CONSTRAINT `fk_application_link` FOREIGN KEY (`course_title`,`university_name`,`student_username`,`creation_date`) REFERENCES `application` (`course_title`, `university_name`, `student_username`, `creation_date`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `application_notification`
--
ALTER TABLE `application_notification`
  ADD CONSTRAINT `fk_appplication_notification` FOREIGN KEY (`course_title`,`university_name`,`student_username`,`creation_date`) REFERENCES `application` (`course_title`, `university_name`, `student_username`, `creation_date`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `course`
--
ALTER TABLE `course`
  ADD CONSTRAINT `fk_university` FOREIGN KEY (`university_name`) REFERENCES `university` (`name`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `course_notification`
--
ALTER TABLE `course_notification`
  ADD CONSTRAINT `fk_course_notification` FOREIGN KEY (`course_title`,`university_name`) REFERENCES `course` (`title`, `university_name`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `course_tags`
--
ALTER TABLE `course_tags`
  ADD CONSTRAINT `fk_course_tags` FOREIGN KEY (`course_title`,`university_name`) REFERENCES `course` (`title`, `university_name`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `document_requirement`
--
ALTER TABLE `document_requirement`
  ADD CONSTRAINT `fk_course` FOREIGN KEY (`course_title`,`university_name`) REFERENCES `course` (`title`, `university_name`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `interested_student`
--
ALTER TABLE `interested_student`
  ADD CONSTRAINT `fk_inter_cour` FOREIGN KEY (`course_name`,`university_name`) REFERENCES `course` (`title`, `university_name`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_inter_stu` FOREIGN KEY (`username`) REFERENCES `student` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `lesson`
--
ALTER TABLE `lesson`
  ADD CONSTRAINT `fk_tutor` FOREIGN KEY (`tutor_username`) REFERENCES `tutor` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `lesson_notification`
--
ALTER TABLE `lesson_notification`
  ADD CONSTRAINT `fk_lesson_notification` FOREIGN KEY (`start`,`tutor_username`) REFERENCES `lesson` (`start`, `tutor_username`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_username` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `reservation`
--
ALTER TABLE `reservation`
  ADD CONSTRAINT `fk_lesson` FOREIGN KEY (`tutor_username_lesson`,`start_date_time`) REFERENCES `lesson` (`tutor_username`, `start`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_student` FOREIGN KEY (`student_username`) REFERENCES `student` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `reservation_notification`
--
ALTER TABLE `reservation_notification`
  ADD CONSTRAINT `fk_reservation_notification` FOREIGN KEY (`student_username`,`tutor_username`,`start`) REFERENCES `reservation` (`student_username`, `tutor_username_lesson`, `start_date_time`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `student_interest`
--
ALTER TABLE `student_interest`
  ADD CONSTRAINT `fk_stu_interest` FOREIGN KEY (`student_username`) REFERENCES `student` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `text_requirement`
--
ALTER TABLE `text_requirement`
  ADD CONSTRAINT `fk_course_requirement` FOREIGN KEY (`course_title`,`university_name`) REFERENCES `course` (`title`, `university_name`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `tutor`
--
ALTER TABLE `tutor`
  ADD CONSTRAINT `fk_user_tutor` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `university_employee`
--
ALTER TABLE `university_employee`
  ADD CONSTRAINT `fk_uni_employee` FOREIGN KEY (`university_name`) REFERENCES `university` (`name`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_user_employee` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Limiti per la tabella `user_notification`
--
ALTER TABLE `user_notification`
  ADD CONSTRAINT `fk_user_username_notification` FOREIGN KEY (`sender_username`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
